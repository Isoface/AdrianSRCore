package es.outlook.adriansrj.core.util.configurable;

import es.outlook.adriansrj.core.util.loadable.Loadable;
import es.outlook.adriansrj.core.util.saveable.Savable;

/**
 * Simple interface that represents Objects that can be loaded by
 * {@link Loadable} interface, and saved by {@link Savable} interface.
 * <p>
 * @author AdrianSR / Thursday 16 April, 2020 / 01:33 PM
 */
public interface Configurable extends Loadable , Savable {}