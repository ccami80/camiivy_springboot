package com.cami.cami_springboot.api.product.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductInquiryRequest {
    private String title;
    private String content;
    private Boolean emailReply;
    private Boolean secret;
}
