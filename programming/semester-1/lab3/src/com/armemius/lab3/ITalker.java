package com.armemius.lab3;

public interface ITalker {
    void tell(ITalker name, String text, ITalker... companions);
}
