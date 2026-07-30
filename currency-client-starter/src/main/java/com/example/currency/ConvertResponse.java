package com.example.currency;

import java.math.BigDecimal;

public record ConvertResponse(boolean success, BigDecimal result) {
}
