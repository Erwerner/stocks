package application.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Wkn {
    private final String wkn;
    private final String wknName;
    private final String wknType;
    private final String wknUrl;
}
