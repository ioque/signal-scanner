package ru.ioque.investfund.application.api.command;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.ApplicationLog;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Result {
    ResultType type;
    List<ApplicationLog> logs;

    public static Result success() {
        return new Result(ResultType.SUCCESS, List.of());
    }
    public static Result success(List<ApplicationLog> logs) {
        return new Result(ResultType.SUCCESS, logs);
    }
    public static Result error(List<ApplicationLog> logs) {
        return new Result(ResultType.ERROR, logs);
    }

    public boolean isSuccess() {
        return type.equals(ResultType.SUCCESS);
    }

    public enum ResultType {
        SUCCESS,
        ERROR
    }
}
