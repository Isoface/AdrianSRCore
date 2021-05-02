package com.hotmail.adriansr.core.util.configurable;

import com.hotmail.adriansr.core.util.loadable.Loadable;
import com.hotmail.adriansr.core.util.saveable.Saveable;

/**
 * Simple interface that represents Objects that can be loaded by
 * {@link Loadable} interface, and saved by {@link Saveable} interface.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 01:33 PM
 */
public interface Configurable extends Loadable , Saveable {}