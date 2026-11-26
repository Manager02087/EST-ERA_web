package uz.estera.service;

import org.springframework.stereotype.Service;

@Service
public class EscrowService {
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;
    public static final long MAX_PROOF_BYTES = 41_943_040L; // 40 MB

    public record EscrowSplit(long platformFee2Pct, long masterPayout98Pct) {}
    public record CancellationSplit(long clientRefundUzs, long masterCompensationUzs, double traveledFraction) {}

    public EscrowSplit calculateStandardSplit(long totalUzs) {
        long fee = Math.round(totalUzs * 0.02);
        long masterPayout = totalUzs - fee;
        return new EscrowSplit(fee, masterPayout);
    }

    public double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_METERS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     * EST-ERA GPS Cancellation Rule:
     * - Before Master en route: 100% refund to Client.
     * - While Master en route: 50% to 70% refund to Client, 30% to 50% compensation to Master
     *   proportional to Haversine route fraction traveled.
     */
    public CancellationSplit calculateEnRouteCancellation(
            long totalUzs,
            double startLat, double startLng,
            double currentLat, double currentLng,
            double clientLat, double clientLng
    ) {
        double totalRoute = Math.max(1.0, haversineMeters(startLat, startLng, clientLat, clientLng));
        double remaining = haversineMeters(currentLat, currentLng, clientLat, clientLng);
        double traveled = Math.max(0.0, Math.min(1.0, 1.0 - (remaining / totalRoute)));

        // Linear map: 0% traveled -> 70% client / 30% master; 100% traveled -> 50% client / 50% master
        double clientShare = 0.70 - (0.20 * traveled);
        long clientRefund = Math.round(totalUzs * clientShare);
        long masterComp = totalUzs - clientRefund;
        return new CancellationSplit(clientRefund, masterComp, traveled);
    }
}
