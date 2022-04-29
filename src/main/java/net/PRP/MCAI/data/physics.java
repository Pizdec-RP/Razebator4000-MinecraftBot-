package net.PRP.MCAI.data;

public class physics {
	public static final double gravity = 0.08; // blocks/tick^2 https://minecraft.gamepedia.com/Entity#Motion_of_entities
	public static final  double airdrag = Math.round(1 - 0.02); // actually (1 - drag)
	public static final  double yawSpeed = 3.0;
	public static final  double pitchSpeed = 3.0;
	public static final  double playerSpeed = 0.1;
	public static final  double sprintSpeed = 0.3;
	public static final  double sneakSpeed = 0.3;
	public static final  double stepHeight = 0.6; // how much height can the bot step on without jump
	public static final  double negligeableVelocity = 0.003; // actually 0.005 for 1.8; but seems fine
	public static final  double soulsandSpeed = 0.4;
	public static final  double honeyblockSpeed = 0.4;
	public static final  double honeyblockJumpSpeed = 0.4;
	public static final  double ladderMaxSpeed = 0.15;
	public static final  double ladderClimbSpeed = 0.2;
	public static final  double playerHalfWidth = 0.3;
	public static final  double playerHeight = 1.8;
	public static final  double waterInertia = 0.8;
	public static final  double lavaInertia = 0.5;
	public static final  double liquidAcceleration = 0.02;
	public static final  double airborneInertia = 0.91;
	public static final  double airborneAcceleration = 0.02;
	public static final  double defaultSlipperiness = 0.6;
	public static final  double outOfLiquidImpulse = 0.3;
	public static final  double autojumpCooldown = 10; // ticks (0.5s)
    public static final  double slowFalling = 0.125;
    public static final  double movementSpeedAttribute = 0.1;
    public static final  String sprintingUUID = "662a6b8d-da3e-4c1c-8813-96ea6097278d"; // SPEED_MODIFIER_SPRINTING_UUID is from LivingEntity.java
    
    public static final  double waterGravity = gravity / 16;
    public static final  double lavaGravity = gravity / 4;
}