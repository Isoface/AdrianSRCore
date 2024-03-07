package es.outlook.adriansrj.core.command;

/**
 * An implementation of {@link CommandArgument} intended for sending help messages.
 * <p>
 * @author AdrianSR / Wednesday 15 April, 2020 / 11:42 AM
 */
public abstract class CommandHelpArgument implements CommandArgument {

	@Override
	public final String getName() {
		return "help";
	}
}