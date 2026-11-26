package uz.estera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.Order;
import uz.estera.repository.OrderRepository;
import uz.estera.service.EscrowService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderRepository orders;
    private final EscrowService escrow;

    public OrderController(OrderRepository orders, EscrowService escrow) {
        this.orders = orders;
        this.escrow = escrow;
    }

    public record CreateOrderReq(
            Integer serviceId, String tashkentDistrict, String addressText,
            Double clientLat, Double clientLng, long amountUzs, String paymentMethod
    ) {}

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderReq req, Authentication auth) {
        UUID clientId = UUID.fromString(auth.getName());
        var split = escrow.calculateStandardSplit(req.amountUzs());

        Order o = new Order();
        o.setOrderCode("EST-" + (100000 + (int) (Math.random() * 900000)));
        o.setClientId(clientId);
        o.setServiceId(req.serviceId());
        o.setTashkentDistrict(req.tashkentDistrict());
        o.setAddressText(req.addressText());
        o.setClientLat(req.clientLat());
        o.setClientLng(req.clientLng());
        o.setBaseAmountUzs(req.amountUzs());
        o.setTotalAmountUzs(req.amountUzs());
        o.setPlatformFeeUzs(split.platformFee2Pct());
        o.setMasterPayoutUzs(split.masterPayout98Pct());
        o.setPaymentMethod(req.paymentMethod() != null ? req.paymentMethod() : "TEST_MODE");
        o.setStatus("ESCROW_HELD");
        return orders.save(o);
    }

    @GetMapping("/my")
    public List<Order> myOrders(Authentication auth) {
        UUID uid = UUID.fromString(auth.getName());
        return orders.findByClientIdOrderByCreatedAtDesc(uid);
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmCompletion(@PathVariable UUID id, Authentication auth) {
        UUID uid = UUID.fromString(auth.getName());
        return orders.findById(id).map(o -> {
            if (uid.equals(o.getClientId())) o.setClientConfirmedAt(OffsetDateTime.now());
            if (uid.equals(o.getMasterId())) o.setMasterConfirmedAt(OffsetDateTime.now());
            if (o.getClientConfirmedAt() != null && o.getMasterConfirmedAt() != null) {
                o.setStatus("COMPLETED");
            }
            orders.save(o);
            return ResponseEntity.ok(Map.of("orderCode", o.getOrderCode(), "status", o.getStatus()));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/grant-support-chat")
    public ResponseEntity<?> grantSupportChatAccess(@PathVariable UUID id) {
        return orders.findById(id).map(o -> {
            o.setSupportChatAccessGranted(true);
            orders.save(o);
            return ResponseEntity.ok(Map.of("orderCode", o.getOrderCode(), "supportChatAccessGranted", true));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable UUID id) {
        return orders.findById(id).map(o -> {
            if ("MASTER_EN_ROUTE".equals(o.getStatus())
                    && o.getMasterStartLat() != null && o.getMasterCurrentLat() != null && o.getClientLat() != null) {
                var split = escrow.calculateEnRouteCancellation(
                        o.getTotalAmountUzs(),
                        o.getMasterStartLat(), o.getMasterStartLng(),
                        o.getMasterCurrentLat(), o.getMasterCurrentLng(),
                        o.getClientLat(), o.getClientLng()
                );
                o.setStatus("REFUNDED");
                orders.save(o);
                return ResponseEntity.ok(split);
            }
            o.setStatus("REFUNDED");
            orders.save(o);
            return ResponseEntity.ok(Map.of("clientRefundUzs", o.getTotalAmountUzs(), "masterCompensationUzs", 0));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
