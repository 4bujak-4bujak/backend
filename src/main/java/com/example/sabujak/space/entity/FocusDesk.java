package com.example.sabujak.space.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "focus_desk")
public class FocusDesk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "focus_desk_id")
    private Long focusDeskId;


    //얘는 좀더 고민하다가 나중에 FocusDeskRoom을 따로 추가?
}
