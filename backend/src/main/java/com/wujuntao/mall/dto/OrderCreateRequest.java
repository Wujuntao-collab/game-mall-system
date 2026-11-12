package com.wujuntao.mall.dto;

import java.util.List;

public record OrderCreateRequest(String address, List<OrderItemRequest> items) {
}
