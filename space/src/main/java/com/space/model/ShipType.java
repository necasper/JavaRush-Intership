package com.space.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Enumerated;

public enum ShipType {
    TRANSPORT,
    MILITARY,
    MERCHANT;
}