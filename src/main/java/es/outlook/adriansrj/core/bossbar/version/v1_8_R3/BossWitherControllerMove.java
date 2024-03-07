package es.outlook.adriansrj.core.bossbar.version.v1_8_R3;

import net.minecraft.server.v1_8_R3.ControllerMove;

/**
 * TODO: Description
 * <br>
 * @author AdrianSR / Sunday 07 February, 2021 / 11:25 AM
 */
public class BossWitherControllerMove extends ControllerMove {

	public BossWitherControllerMove ( BossWither boss ) {
		super ( boss );
	}
	
	@Override // isUpdating
	public boolean a ( ) {
		return false;
	}
	
	@Override // getSpeed
	public double b ( ) {
		return 0.0D;
	}
	
	@Override // setMoveTo
	public void a ( double arg0 , double arg1 , double arg2 , double arg3 ) {
		// ignoring
	}
	
	@Override
	public void c ( ) {
		this.a.n ( 0.0F );
	}
}