package pizdecrp.MCAI.inventory;

import pizdecrp.MCAI.utils.CraftingRecipe;

public interface ICraftable {

	public static void craft(CraftingRecipe recipe) {
		System.out.println(recipe.getLines());
	}
	
}
