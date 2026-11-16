
package com.shareplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TrackingService {

    public record TrackingSessionCredentials(
            long trackingId,
            String donorToken,
            String ngoToken) {
    }

    // Original method retained for compatibility.
    public TrackingSessionCredentials createTrackingSessionWithTokens(
            int donationId,
            String donorName,
            String ngoName,
            double pickupLatitude,
            double pickupLongitude,
            double deliveryLatitude,
            double deliveryLongitude) throws SQLException {

        return createTrackingSessionWithTokens(
                donationId,
                donorName,
                ngoName,
                null,
                pickupLatitude,
                pickupLongitude,
                deliveryLatitude,
                deliveryLongitude
        );
    }

    // Method with custom delivery address.
    public TrackingSessionCredentials createTrackingSessionWithTokens(
            int donationId,
            String donorName,
            String ngoName,
            String deliveryAddress,
            Double pickupLatitude,
            Double pickupLongitude,
            Double deliveryLatitude,
            Double deliveryLongitude) throws SQLException {

        try (Connection con = DBConnection.getConnection()) {
            return createTrackingSessionWithTokens(
                    con,
                    donationId,
                    donorName,
                    ngoName,
                    deliveryAddress,
                    pickupLatitude,
                    pickupLongitude,
                    deliveryLatitude,
                    deliveryLongitude
            );
        }
    }

    // Uses an existing connection for transaction support.
    public TrackingSessionCredentials createTrackingSessionWithTokens(
            Connection con,
            int donationId,
            String donorName,
            String ngoName,
            String deliveryAddress,
            Double pickupLatitude,
            Double pickupLongitude,
            Double deliveryLatitude,
            Double deliveryLongitude) throws SQLException {

        String donorToken = TrackingTokenUtil.generateToken();
        String ngoToken = TrackingTokenUtil.generateToken();

        String donorHash = TrackingTokenUtil.hashToken(donorToken);
        String ngoHash = TrackingTokenUtil.hashToken(ngoToken);

        String sql = """
            INSERT INTO tracking_sessions
            (donation_id, donor_name, ngo_name,
             pickup_latitude, pickup_longitude,
             delivery_latitude, delivery_longitude,
             delivery_address, journey_status, active_leg,
             donor_tracking_token_hash,
             ngo_tracking_token_hash)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'ACCEPTED', 'PICKUP', ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, donationId);
            ps.setString(2, donorName);
            ps.setString(3, ngoName);

            setNullableDouble(ps, 4, pickupLatitude);
            setNullableDouble(ps, 5, pickupLongitude);
            setNullableDouble(ps, 6, deliveryLatitude);
            setNullableDouble(ps, 7, deliveryLongitude);

            ps.setString(8, deliveryAddress);
            ps.setString(9, donorHash);
            ps.setString(10, ngoHash);

            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new TrackingSessionCredentials(
                            rs.getLong(1),
                            donorToken,
                            ngoToken
                    );
                }
            }
        }

        throw new SQLException("Could not create tracking session.");
    }

    private void setNullableDouble(
            PreparedStatement ps,
            int index,
            Double value) throws SQLException {

        if (value == null) {
            ps.setNull(index, java.sql.Types.DECIMAL);
        } else {
            ps.setDouble(index, value);
        }
    }

    // Compatibility method for existing code.
    public long createTrackingSession(
            int donationId,
            String donorName,
            String ngoName,
            double pickupLatitude,
            double pickupLongitude,
            double deliveryLatitude,
            double deliveryLongitude) throws SQLException {

        TrackingSessionCredentials session =
                createTrackingSessionWithTokens(
                        donationId,
                        donorName,
                        ngoName,
                        pickupLatitude,
                        pickupLongitude,
                        deliveryLatitude,
                        deliveryLongitude
                );

        return session.trackingId();
    }

    public void saveLocation(
            long trackingId,
            String sharedBy,
            double latitude,
            double longitude,
            double accuracy) throws SQLException {

        if (!"DONOR".equals(sharedBy) &&
                !"NGO".equals(sharedBy)) {
            throw new IllegalArgumentException(
                    "Invalid location sharing role.");
        }

        String sql = """
            INSERT INTO tracking_locations
            (tracking_id, shared_by, latitude, longitude, accuracy)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, trackingId);
            ps.setString(2, sharedBy);
            ps.setDouble(3, latitude);
            ps.setDouble(4, longitude);
            ps.setDouble(5, accuracy);

            ps.executeUpdate();
        }
    }

    public void setSharing(
            long trackingId,
            String role,
            boolean enabled) throws SQLException {

        String column;

        if ("DONOR".equals(role)) {
            column = "donor_sharing";
        } else if ("NGO".equals(role)) {
            column = "ngo_sharing";
        } else {
            throw new IllegalArgumentException("Invalid role.");
        }

        String sql = "UPDATE tracking_sessions SET "
                + column + " = ? WHERE tracking_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBoolean(1, enabled);
            ps.setLong(2, trackingId);
            ps.executeUpdate();
        }
    }

    public void updateJourneyStatus(
            long trackingId,
            String journeyStatus,
            String activeLeg) throws SQLException {

        String sql = """
            UPDATE tracking_sessions
            SET journey_status = ?, active_leg = ?
            WHERE tracking_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, journeyStatus);
            ps.setString(2, activeLeg);
            ps.setLong(3, trackingId);

            ps.executeUpdate();
        }
    }
}
