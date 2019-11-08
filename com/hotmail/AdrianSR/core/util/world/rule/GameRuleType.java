package com.hotmail.AdrianSR.core.util.world.rule;

import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.metadata.MetadataValue;

import com.hotmail.AdrianSR.core.main.core.AdrianSRCore;
import com.hotmail.AdrianSR.core.util.classes.DataType;
import com.hotmail.AdrianSR.core.util.world.rule.metadata.GameRuleMetadata;

public enum GameRuleType {

	FIRE_TICK {
		public String getName() { return "doFireTick"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	MOB_GRIEFING {
		public String getName() { return "mobGriefing"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	KEEP_INVENTORY {
		public String getName() { return "keepInventory"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	MOB_SPAWNING {
		public String getName() { return "doMobSpawning"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	MOB_LOOT {
		public String getName() { return "doMobLoot"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	TILE_DROPS {
		public String getName() { return "doTileDrops"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	ENTITY_DROPS {
		public String getName() { return "doEntityDrops"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	COMMAND_BLOCK_OUTPUT {
		public String getName() { return "commandBlockOutput"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	NATURAL_REGENERATION {
		public String getName() { return "naturalRegeneration"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	DAYLIGHT_CYCLE {
		public String getName() { return "doDaylightCycle"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	LOG_ADMIN_COMMANDS {
		public String getName() { return "logAdminCommands"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	SHOW_DEATH_MESSAGES {
		public String getName() { return "showDeathMessages"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	RANDOM_TICK_SPEED {
		public String getName() { return "randomTickSpeed"; }
		public boolean isBooleanValue() { return false; }
		public boolean isNumericalValue() { return true; }
		public int getDefaultNumericalValue() { return 3; }
	},
	
	SEND_COMMAND_FEEDBACK {
		public String getName() { return "sendCommandFeedback"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	REDUCED_DEBUG_INFO {
		public String getName() { return "reducedDebugInfo"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	SPECTATORS_GENERATE_CHUNKS {
		public String getName() { return "spectatorsGenerateChunks"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return true; }
		public boolean isNumericalValue() { return false; }
	},
	
	SPAWN_RADIUS {
		public String getName() { return "randomTickSpeed"; }
		public boolean isBooleanValue() { return false; }
		public boolean isNumericalValue() { return true; }
		public int getDefaultNumericalValue() { return 10; }
	},
	
	DISABLE_ELYTRA_MOVEMENT_CHECK {
		public String getName() { return "disableElytraMovementCheck"; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	DISABLE_BLOCK_PLACING {
		public String getName() { return "disableBlockPlacing"; }
		public GameRulePresentationMode getPresentatioMode() { return GameRulePresentationMode.METADATA; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	DISABLE_BLOCK_BREAKING {
		public String getName() { return "disableBlockBreaking"; }
		public GameRulePresentationMode getPresentatioMode() { return GameRulePresentationMode.METADATA; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	INVULNERABLE_ENTITIES {
		public String getName() { return "invulnerableEntities"; }
		public GameRulePresentationMode getPresentatioMode() { return GameRulePresentationMode.METADATA; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	DISALLOW_PVP {
		public String getName() { return "disallowPvP"; }
		public GameRulePresentationMode getPresentatioMode() { return GameRulePresentationMode.METADATA; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	
	DISABLE_HUNGER {
		public String getName() { return "disableHunger"; }
		public GameRulePresentationMode getPresentatioMode() { return GameRulePresentationMode.METADATA; }
		public boolean isBooleanValue() { return true; }
		public boolean getDefaultBooleanValue() { return false; }
		public boolean isNumericalValue() { return false; }
	},
	;
	
	public String getName() {
		throw new UnsupportedOperationException("|°|");
	}

	public DataType getDataType() {
		return isBooleanValue() ? DataType.BOOLEAN : DataType.INTEGER;
	}
	
	public GameRulePresentationMode getPresentatioMode() {
		return GameRulePresentationMode.ORDINARY;
	}
	
	public Object getValue(World world) {
		if (!isPresent(world)) {
			return null;
		}
		
		if (getPresentatioMode() == GameRulePresentationMode.ORDINARY) {
			return world.getGameRuleValue(getName());
		} else {
			return getGameRuleMetadata(world) != null ? getGameRuleMetadata(world).value() : null;
		}
	}
	
	public GameRuleMetadata getGameRuleMetadata(World world) {
		Validate.isTrue(getPresentatioMode() == GameRulePresentationMode.METADATA, String.format(
				"The presentation type of type game rule type is not '%s'", GameRulePresentationMode.METADATA.name()));
		for (MetadataValue metadata : world.getMetadata(getName())
				.stream()
				.filter(other -> other instanceof GameRuleMetadata)
				.collect(Collectors.toList())) {
			if (((GameRuleMetadata) metadata).getType() == this) {
				return (GameRuleMetadata) metadata;
			}
		}
		return null;
	}
	
	public boolean isPresent(World world) {
		return getPresentatioMode() == GameRulePresentationMode.METADATA ? getGameRuleMetadata(world) != null : false;
	}
	
	public boolean isSameDataType(Object value) {
		return ( value != null ) ? ( getDataType() == DataType.fromClass(value.getClass()) ) : ( false );
	}
	
	public boolean isBooleanValue() {
		throw new UnsupportedOperationException("|°|");
	}
	
	public boolean getDefaultBooleanValue() {
		throw new UnsupportedOperationException("This is not the type of value for this game rule!");
	}
	
	public boolean isNumericalValue() {
		throw new UnsupportedOperationException("|°|");
	}
	
	public int getDefaultNumericalValue() {
		throw new UnsupportedOperationException("This is not the type of value for this game rule!");
	}
	
	public void apply(World world, Object value) {
		Validate.isTrue( isSameDataType(value), "This is not the type of value for this game rule!" );
		
		if (getPresentatioMode() == GameRulePresentationMode.ORDINARY) { // ordinary presentation mode
			world.setGameRuleValue(getName(), value.toString());
		} else { // metadata presentation mode
			world.setMetadata(getName(), new GameRuleMetadata(AdrianSRCore.getInstance(), this, value));
		}
	}
}