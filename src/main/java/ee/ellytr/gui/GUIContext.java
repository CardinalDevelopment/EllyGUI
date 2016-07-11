package ee.ellytr.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GUIContext<T> {

  private final T node;

  private final int position;
  private final boolean defaultContext;

}
