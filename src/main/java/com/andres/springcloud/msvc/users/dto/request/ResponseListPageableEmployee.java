package com.andres.springcloud.msvc.users.dto.request;

import com.andres.springcloud.msvc.users.dto.EmployeeDto;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseListPageableEmployee {
    private List<EmployeeDto> employeeDtoList;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean end;
}
