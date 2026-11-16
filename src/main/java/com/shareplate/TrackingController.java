
package com.shareplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    public record LocationRequest(
            double latitude,
            double longitude,
            double accuracy) {
    }

    public record LaunchRequest(
            long trackingId,
            String role,
            String launchCode) {
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "application", "SharePlate",
            "service", "Live Tracking API",
            "status", "Running"
        );
    }

    // Extract a Bearer token from the Authorization header.
    private String extractToken(String authorization) {
        if (authorization == null ||
            !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7).trim();
    }

    // Validate either a permanent token or an unexpired launch code.
    private boolean authenticate(
            long trackingId,
            String role,
            String token) throws SQLException {

        if (trackingId <= 0 || token == null || token.isBlank()) {
            return false;
        }

        String permanentColumn;
        String launchColumn;
        String expiryColumn;

        if ("DONOR".equals(role)) {
            permanentColumn = "donor_tracking_token_hash";
            launchColumn = "donor_launch_hash";
            expiryColumn = "donor_launch_expires_at";
        } else if ("NGO".equals(role)) {
            permanentColumn = "ngo_tracking_token_hash";
            launchColumn = "ngo_launch_hash";
            expiryColumn = "ngo_launch_expires_at";
        } else {
            return false;
        }

        String sql = "SELECT " + permanentColumn + ", "
                   + launchColumn + ", " + expiryColumn
                   + " FROM tracking_sessions WHERE tracking_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, trackingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                String permanentHash = rs.getString(1);

                if (TrackingTokenUtil.verifyToken(token, permanentHash)) {
                    return true;
                }

                String launchHash = rs.getString(2);
                Timestamp expiry = rs.getTimestamp(3);

                return expiry != null
                    && expiry.toInstant().isAfter(Instant.now())
                    && TrackingTokenUtil.verifyToken(token, launchHash);
            }
        }
    }

    // Validate a temporary launch code before opening the tracking page.
    @PostMapping("/launch/validate")
    public ResponseEntity<?> validateLaunch(
            @RequestBody LaunchRequest request) {

        try {
            if (request == null ||
                request.trackingId() <= 0 ||
                request.role() == null ||
                request.launchCode() == null ||
                request.launchCode().isBlank()) {

                return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid launch details.")
                );
            }

            if (!"DONOR".equals(request.role()) &&
                !"NGO".equals(request.role())) {

                return ResponseEntity.badRequest().body(
                    Map.of("error", "Invalid participant role.")
                );
            }

            String launchColumn;
            String expiryColumn;

            if ("DONOR".equals(request.role())) {
                launchColumn = "donor_launch_hash";
                expiryColumn = "donor_launch_expires_at";
            } else {
                launchColumn = "ngo_launch_hash";
                expiryColumn = "ngo_launch_expires_at";
            }

            String sql = "SELECT " + launchColumn + ", "
                       + expiryColumn
                       + " FROM tracking_sessions WHERE tracking_id = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setLong(1, request.trackingId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                            Map.of("error", "Invalid or expired launch code.")
                        );
                    }

                    String storedHash = rs.getString(1);
                    Timestamp expiry = rs.getTimestamp(2);

                    boolean valid = expiry != null
                        && expiry.toInstant().isAfter(Instant.now())
                        && TrackingTokenUtil.verifyToken(
                            request.launchCode(), storedHash
                        );

                    if (!valid) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                            Map.of("error", "Invalid or expired launch code.")
                        );
                    }
                }
            }

            return ResponseEntity.ok(
                Map.of(
                    "message", "Launch code verified.",
                    "trackingId", request.trackingId(),
                    "role", request.role()
                )
            );

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Database operation failed.")
            );
        }
    }

    // Check whether the participant has enabled location sharing.
    private boolean isSharingEnabled(
            long trackingId,
            String role) throws SQLException {

        String column;

        if ("DONOR".equals(role)) {
            column = "donor_sharing";
        } else if ("NGO".equals(role)) {
            column = "ngo_sharing";
        } else {
            return false;
        }

        String sql = "SELECT " + column
                   + ", journey_status FROM tracking_sessions"
                   + " WHERE tracking_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, trackingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("journey_status");

                    return rs.getBoolean(1)
                        && !"COMPLETED".equalsIgnoreCase(status)
                        && !"CANCELLED".equalsIgnoreCase(status);
                }
            }
        }

        return false;
    }

    // Submit a GPS location.
    
@PostMapping("/{trackingId}/location")
public ResponseEntity<?> updateLocation(
        @PathVariable("trackingId") long trackingId,
        @RequestParam("role") String role,
        @RequestHeader("Authorization") String authorization,
        @RequestBody LocationRequest location) {

    try {
        String token = extractToken(authorization);

        if (trackingId <= 0 ||
            (!"DONOR".equals(role) && !"NGO".equals(role)) ||
            location == null ||
            !Double.isFinite(location.latitude()) ||
            !Double.isFinite(location.longitude()) ||
            location.latitude() < -90 ||
            location.latitude() > 90 ||
            location.longitude() < -180 ||
            location.longitude() > 180 ||
            !Double.isFinite(location.accuracy()) ||
            location.accuracy() < 0) {

            return ResponseEntity.badRequest().body(
                Map.of("error", "Invalid location data.")
            );
        }

        if (!authenticate(trackingId, role, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Invalid tracking credentials.")
            );
        }

        if (!isSharingEnabled(trackingId, role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("error", "Location sharing is disabled or journey ended.")
            );
        }

        new TrackingService().saveLocation(
            trackingId,
            role,
            location.latitude(),
            location.longitude(),
            location.accuracy()
        );

        return ResponseEntity.ok(
            Map.of("message", "Location saved successfully.")
        );

    } catch (SQLException e) {
        e.printStackTrace();

        return ResponseEntity.internalServerError().body(
            Map.of("error", "Database operation failed.")
        );
    }
}


    // Retrieve recent locations from participants who enabled sharing.
    
@GetMapping("/{trackingId}/locations")
public ResponseEntity<?> getLocations(
        @PathVariable("trackingId") long trackingId,
        @RequestParam("role") String role,
        @RequestHeader("Authorization") String authorization) {

    try {
        String token = extractToken(authorization);

        if (trackingId <= 0 ||
            (!"DONOR".equals(role) && !"NGO".equals(role)) ||
            !authenticate(trackingId, role, token)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Invalid tracking credentials.")
            );
        }

        String sql = """
            SELECT l.shared_by, l.latitude, l.longitude,
                   l.accuracy, l.recorded_at
            FROM tracking_locations l
            JOIN tracking_sessions s
              ON l.tracking_id = s.tracking_id
            WHERE l.tracking_id = ?
              AND (
                (l.shared_by = 'DONOR' AND s.donor_sharing = TRUE)
                OR
                (l.shared_by = 'NGO' AND s.ngo_sharing = TRUE)
              )
            ORDER BY l.recorded_at DESC
            LIMIT 100
            """;

        List<Map<String, Object>> locations = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, trackingId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> location = new HashMap<>();

                    location.put("sharedBy", rs.getString("shared_by"));
                    location.put("latitude", rs.getBigDecimal("latitude"));
                    location.put("longitude", rs.getBigDecimal("longitude"));
                    location.put("accuracy", rs.getBigDecimal("accuracy"));

                    Timestamp recordedAt = rs.getTimestamp("recorded_at");
                    location.put("recordedAt",
                        recordedAt != null ? recordedAt.toString() : null);

                    locations.add(location);
                }
            }
        }

        return ResponseEntity.ok(locations);

    } catch (SQLException e) {
        e.printStackTrace();

        return ResponseEntity.internalServerError().body(
            Map.of("error", "Database operation failed.")
        );
    }
}


    // Enable or disable location sharing.
   
@PostMapping("/{trackingId}/sharing")
public ResponseEntity<?> updateSharing(
        @PathVariable("trackingId") long trackingId,
        @RequestParam("role") String role,
        @RequestParam("enabled") boolean enabled,
        @RequestHeader("Authorization") String authorization) {

    try {
        String token = extractToken(authorization);

        if (trackingId <= 0 ||
            (!"DONOR".equals(role) && !"NGO".equals(role))) {

            return ResponseEntity.badRequest().body(
                Map.of("error", "Invalid tracking details.")
            );
        }

        if (!authenticate(trackingId, role, token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("error", "Invalid tracking credentials.")
            );
        }

        new TrackingService().setSharing(
            trackingId, role, enabled
        );

        return ResponseEntity.ok(
            Map.of(
                "message", enabled
                    ? "Location sharing enabled."
                    : "Location sharing disabled.",
                "sharing", enabled
            )
        );

    } catch (SQLException e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(
            Map.of("error", "Database operation failed.")
        );
    }
}

}
