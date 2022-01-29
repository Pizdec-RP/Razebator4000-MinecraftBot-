package net.PRP.MCAI.utils;

public class physics {
	public static double gravity = 0.08; // blocks/tick^2 https://minecraft.gamepedia.com/Entity#Motion_of_entities
	public static double airdrag = Math.round(1 - 0.02); // actually (1 - drag)
	public static double yawSpeed = 3.0;
	public static double pitchSpeed = 3.0;
	public static double playerSpeed = 0.1;
	public static double sprintSpeed = 0.3;
	public static double sneakSpeed = 0.3;
	public static double stepHeight = 0.6; // how much height can the bot step on without jump
	public static double negligeableVelocity = 0.003; // actually 0.005 for 1.8; but seems fine
	public static double soulsandSpeed = 0.4;
	public static double honeyblockSpeed = 0.4;
	public static double honeyblockJumpSpeed = 0.4;
	public static double ladderMaxSpeed = 0.15;
	public static double ladderClimbSpeed = 0.2;
	public static double playerHalfWidth = 0.3;
	public static double playerHeight = 1.8;
	public static double waterInertia = 0.8;
	public static double lavaInertia = 0.5;
	public static double liquidAcceleration = 0.02;
	public static double airborneInertia = 0.91;
	public static double airborneAcceleration = 0.02;
	public static double defaultSlipperiness = 0.6;
	public static double outOfLiquidImpulse = 0.3;
	public static double autojumpCooldown = 10; // ticks (0.5s)
    public static double slowFalling = 0.125;
    public static double movementSpeedAttribute = 0.1;
    public static String sprintingUUID = "662a6b8d-da3e-4c1c-8813-96ea6097278d"; // SPEED_MODIFIER_SPRINTING_UUID is from LivingEntity.java
    
    public static double waterGravity = gravity / 16;
    public static double lavaGravity = gravity / 4;
}