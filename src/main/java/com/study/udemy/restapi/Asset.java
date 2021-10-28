package com.study.udemy.restapi;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record Asset(UUID id, @NotNull String name) {
}
