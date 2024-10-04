package org.dataProcessor.importData;

import lombok.extern.slf4j.Slf4j;
import org.dataProcessor.model.Starship;
import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomSkipListener implements SkipListener <Starship, Starship>{

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("an error occurred on reading ===> {}", t.getMessage());
    }
    @Override
    public void onSkipInWrite(Starship item, Throwable t) {
        log.warn("an error occurred on writing of: {} ===> {}",item, t.getMessage());
    }

    @Override
    public void onSkipInProcess(Starship item, Throwable t) {
        log.warn("an error occurred on processing of: {} ===> {}",item, t.getMessage());
    }
}
