package ru.ioque.investfund.application.api.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public abstract class Command {
    private UUID track;

    public Command() {
        throw new IllegalArgumentException();
    }

    public Command(UUID track) {
        this.track = track;
        if (track == null) {
            throw new IllegalArgumentException();
        }
    }
}
