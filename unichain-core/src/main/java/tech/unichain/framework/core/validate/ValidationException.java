package tech.unichain.framework.core.validate;


import tech.unichain.framework.core.BusinessException;

import java.util.List;

public class ValidationException extends BusinessException {
    private static final long serialVersionUID = 7807607467371210082L;
    private ValidateResults results;

    public ValidationException(String message) {
        super(message, 400);
    }

    public ValidationException(String message, String field) {
        super(message, 400);
        results = new SimpleValidateResults().addResult(field, message);
    }

    public ValidationException(ValidateResults results) {
        super(results.toString(), 400);
        this.results = results;
    }

    public List<ValidateResults.Result> getResults() {
        if (results == null) {
            return null;
        }
        return results.getResults();
    }
}
