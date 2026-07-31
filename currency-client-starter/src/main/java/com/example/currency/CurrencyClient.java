package com.example.currency;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currency-client", url = "${app.currency-client.base-url}")
public interface CurrencyClient {

    @GetMapping("/convert")
    ConvertResponse convert(@RequestParam("access_key") String apikey,
                            @RequestParam("from") String from,
                            @RequestParam("to") String to);
}
