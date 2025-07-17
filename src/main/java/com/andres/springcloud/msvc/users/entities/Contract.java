package com.andres.springcloud.msvc.users.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "contract")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long id;

    @NotNull
    @Column(name = "contract_time_month")
    private Integer timeMonth;

    @NotBlank
    @Column(name = "contract_kind")
    private String kind;

    @NotBlank
    @Column(name = "contract_position")
    private String position;

    @NotNull
    @Column(name = "contract_salary", precision = 15, scale = 2)
    private BigDecimal salary;

    @NotBlank
    @Column(name = "contract_state")
    private String state;

    @NotBlank
    @Column(name = "employee_area")
    private String employeeArea;

    @Column(name = "employee_obs", columnDefinition = "TEXT")
    private String employeeObs;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;
}