package com.example.sabujak.space.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "focus_desk")
public class FocusDesk extends Space {

    @Column(name = "focus_desk_number")
    @Positive
    private int focusDeskNumber;

    @Column(name = "can_reserve")
    @NotNull
    private boolean canReserve = true;
}
