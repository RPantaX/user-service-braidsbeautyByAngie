package com.andres.springcloud.msvc.users.dto.request;

import com.andres.springcloud.msvc.users.dto.CustomerDto;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseListPageableCustomer {
    private List<CustomerDto> customerDtoList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
