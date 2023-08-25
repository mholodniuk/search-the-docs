package com.mholodniuk.searchmedaddy.document.extract;

import java.util.List;

public interface ContentExtractor {
    List<String> extract(byte[] file);
}
