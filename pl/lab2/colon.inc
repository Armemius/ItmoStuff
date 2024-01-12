%define COLON_LABEL 0

%macro colon 2
    %2:
        dq COLON_LABEL
        db %1, 0
    %define COLON_LABEL %2
%endmacro
