package ru.sennov.productranking.grpc;

import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import ru.sennov.productranking.api.dto.ProductClickRequest;
import ru.sennov.productranking.api.dto.ProductClickResponse;
import ru.sennov.productranking.api.dto.SaleAppendRequest;
import ru.sennov.productranking.api.dto.SaleAppendResponse;
import ru.sennov.productranking.api.dto.SaleItemRequest;
import ru.sennov.productranking.grpc.api.ProductClickEventRequest;
import ru.sennov.productranking.grpc.api.ProductClickEventResponse;
import ru.sennov.productranking.grpc.api.ProductEventsServiceGrpc;
import ru.sennov.productranking.grpc.api.SaleEventRequest;
import ru.sennov.productranking.grpc.api.SaleEventResponse;
import ru.sennov.productranking.grpc.api.SaleItem;
import ru.sennov.productranking.service.ProductClickService;
import ru.sennov.productranking.service.SalesService;

@Component
public class ProductEventsGrpcService extends ProductEventsServiceGrpc.ProductEventsServiceImplBase {

    private final ProductClickService clickService;
    private final SalesService salesService;

    public ProductEventsGrpcService(ProductClickService clickService, SalesService salesService) {
        this.clickService = clickService;
        this.salesService = salesService;
    }

    @Override
    public void recordProductClick(ProductClickEventRequest request,
            StreamObserver<ProductClickEventResponse> responseObserver) {
        try {
            ProductClickResponse response = clickService.appendClick(toClickRequest(request));
            responseObserver.onNext(toClickResponse(response));
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(GrpcExceptionMapper.toStatusRuntimeException(exception));
        }
    }

    @Override
    public void recordSaleEvent(SaleEventRequest request, StreamObserver<SaleEventResponse> responseObserver) {
        try {
            SaleAppendResponse response = salesService.appendSale(toSaleRequest(request));
            responseObserver.onNext(toSaleResponse(response));
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(GrpcExceptionMapper.toStatusRuntimeException(exception));
        }
    }

    private ProductClickRequest toClickRequest(ProductClickEventRequest request) {
        ProductClickRequest clickRequest = new ProductClickRequest();
        clickRequest.setProductId(GrpcConverters.requiredUuid(request.getProductId(), "product_id"));
        clickRequest.setUserId(GrpcConverters.optionalUuid(request.getUserId(), "user_id"));
        clickRequest.setClickedAt(GrpcConverters.optionalInstant(request.hasClickedAt(), request.getClickedAt(),
                "clicked_at"));
        return clickRequest;
    }

    private ProductClickEventResponse toClickResponse(ProductClickResponse response) {
        return ProductClickEventResponse.newBuilder()
                .setClickId(GrpcConverters.uuidToString(response.getId()))
                .setProductId(GrpcConverters.uuidToString(response.getProductId()))
                .setProductName(response.getProductName())
                .setUserId(GrpcConverters.uuidToString(response.getUserId()))
                .setClickedAt(GrpcConverters.timestamp(response.getClickedAt()))
                .setCreatedAt(GrpcConverters.timestamp(response.getCreatedAt()))
                .build();
    }

    private SaleAppendRequest toSaleRequest(SaleEventRequest request) {
        SaleAppendRequest saleRequest = new SaleAppendRequest();
        saleRequest.setOrderId(GrpcConverters.optionalUuid(request.getOrderId(), "order_id"));
        saleRequest.setUserId(GrpcConverters.optionalUuid(request.getUserId(), "user_id"));
        saleRequest.setPurchasedAt(GrpcConverters.optionalInstant(request.hasPurchasedAt(), request.getPurchasedAt(),
                "purchased_at"));
        saleRequest.setItems(toSaleItems(request.getItemsList()));
        return saleRequest;
    }

    private List<SaleItemRequest> toSaleItems(List<SaleItem> items) {
        return items.stream()
                .map(this::toSaleItemRequest)
                .collect(Collectors.toList());
    }

    private SaleItemRequest toSaleItemRequest(SaleItem item) {
        SaleItemRequest itemRequest = new SaleItemRequest();
        itemRequest.setProductId(GrpcConverters.requiredUuid(item.getProductId(), "items.product_id"));
        itemRequest.setQuantity(item.getQuantity());
        itemRequest.setPrice(GrpcConverters.optionalBigDecimal(item.getPrice(), "items.price"));
        itemRequest.setTotalPrice(GrpcConverters.optionalBigDecimal(item.getTotalPrice(), "items.total_price"));
        return itemRequest;
    }

    private SaleEventResponse toSaleResponse(SaleAppendResponse response) {
        SaleEventResponse.Builder builder = SaleEventResponse.newBuilder()
                .setOrderId(GrpcConverters.uuidToString(response.getOrderId()))
                .setUserId(GrpcConverters.uuidToString(response.getUserId()))
                .setPurchasedAt(GrpcConverters.timestamp(response.getPurchasedAt()))
                .setTotalPrice(response.getTotalPrice().toPlainString())
                .setItemsCount(response.getItemsCount());

        for (UUID productId : response.getAffectedProductIds()) {
            builder.addAffectedProductIds(GrpcConverters.uuidToString(productId));
        }

        return builder.build();
    }
}
