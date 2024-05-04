package ru.ioque.investfund.application.modules.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.ioque.investfund.domain.core.ApplicationLog;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Result {
    ResultType type;
    RuntimeException throwable;
    List<ApplicationLog> logs = new ArrayList<>();

    public static Result success() {
        return new Result(ResultType.SUCCESS, List.of());
    }

    public static Result success(List<ApplicationLog> logs) {
        return new Result(ResultType.SUCCESS, logs);
    }

    public static Result error(RuntimeException throwable) {
        return new Result(ResultType.ERROR, throwable);
    }

    public void throwError() {
        throw throwable;
    }

    public boolean isSuccess() {
        return type.equals(ResultType.SUCCESS);
    }

    public enum ResultType {
        SUCCESS,
        ERROR
    }

    public Result(ResultType type, RuntimeException throwable) {
        this.type = type;
        this.throwable = throwable;
    }

    public Result(ResultType type, List<ApplicationLog> logs) {
        this.type = type;
        this.logs = logs;
    }
}
