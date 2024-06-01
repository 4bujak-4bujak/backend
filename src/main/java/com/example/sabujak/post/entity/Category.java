package com.example.sabujak.post.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum Category {

    JOB(null),
    OWNER(JOB),
    OFFICE(JOB),
    FINANCE(JOB),
    HRD(JOB),
    PROMOTION(JOB),
    ITDEV(JOB),
    ITPLAN(JOB),
    SALE(JOB),
    DESIGN(JOB),
    SERVICE(JOB),
    CONTENTS(JOB),
    RND(JOB),
    PROFESSIONAL(JOB),
    MD(JOB),
    INSURANCE(JOB),
    ETC(JOB),

    INTEREST(null),
    FREE_BOARD(INTEREST),
    COMPANY_LIFE(INTEREST),
    INVESTMENT_FINANCE(INTEREST),
    STUDY_PROMOTION(INTEREST),
    EXERCISE(INTEREST),
    FOOD(INTEREST),
    CULTURAL_LIFE(INTEREST),
    FAMILY_PET(INTEREST);

    private final Category parent;
    private final List<Category> child;

    Category(Category parent) {
        this.parent = parent;
        this.child = new ArrayList<>();
        if(parent != null) {
            parent.child.add(this);
        }
    }
}
