package com.example.sabujak.post.dto;

import java.util.List;

public record CustomSlice<T>(List<T> content, boolean hasNext) {
}
