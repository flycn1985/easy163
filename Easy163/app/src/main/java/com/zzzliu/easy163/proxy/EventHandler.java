package com.zzzliu.easy163.proxy;

import java.nio.channels.SelectionKey;

public interface EventHandler {
  void execute(SelectionKey key);
}