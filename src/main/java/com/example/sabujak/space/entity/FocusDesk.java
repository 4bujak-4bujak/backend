package com.example.sabujak.space.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "focus_desk")
public class FocusDesk extends Space {

    //얘는 좀더 고민하다가 나중에 FocusDeskRoom을 따로 추가?
}
