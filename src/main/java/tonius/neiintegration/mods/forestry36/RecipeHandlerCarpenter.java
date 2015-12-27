package tonius.neiintegration.mods.forestry36;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import tonius.neiintegration.PositionedFluidTank;
import tonius.neiintegration.RecipeHandlerBase;
import tonius.neiintegration.Utils;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.api.API;
import forestry.core.interfaces.IDescriptiveRecipe;
import forestry.factory.gadgets.MachineCarpenter;

public class RecipeHandlerCarpenter extends RecipeHandlerBase {
    
    private static Class<? extends GuiContainer> guiClass;
    
    @Override
    public void prepare() {
        guiClass = Utils.getClass("forestry.factory.gui.GuiCarpenter");
        API.setGuiOffset(guiClass, 5, 14);
    }
    
    public class CachedCarpenterRecipe extends CachedBaseRecipe {
        
        public List<PositionedStack> inputs = new ArrayList<PositionedStack>();
        public PositionedFluidTank tank;
        public PositionedStack output;
        
        public CachedCarpenterRecipe(MachineCarpenter.Recipe recipe, boolean genPerms) {
            IDescriptiveRecipe irecipe = (IDescriptiveRecipe) recipe.asIRecipe();
            if (irecipe != null) {
                if (irecipe.getIngredients() != null) {
                    this.setIngredients(irecipe.getWidth(), irecipe.getHeight(), irecipe.getIngredients());
                }
                if (recipe.getBox() != null) {
                    this.inputs.add(new PositionedStack(recipe.getBox(), 78, 6));
                }
                if (recipe.getLiquid() != null) {
                    this.tank = new PositionedFluidTank(recipe.getLiquid(), 10000, new Rectangle(145, 3, 16, 58), RecipeHandlerCarpenter.this.getGuiTexture(), new Point(176, 0));
                }
                if (recipe.getCraftingResult() != null) {
                    this.output = new PositionedStack(recipe.getCraftingResult(), 75, 37);
                }
            }
            
            if (genPerms) {
                this.generatePermutations();
            }
        }
        
        public CachedCarpenterRecipe(MachineCarpenter.Recipe recipe) {
            this(recipe, false);
        }
        
        public void setIngredients(int width, int height, Object[] items) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int index = y * width + x;
                    if (index >= items.length) {
                        continue;
                    }
                    
                    Object item = items[index];
                    
                    if (item == null) {
                        continue;
                    } else if (item instanceof ItemStack[] && ((ItemStack[]) item).length == 0) {
                        continue;
                    } else if (item instanceof List && ((List) item).size() == 0) {
                        continue;
                    }
                    
                    PositionedStack stack = new PositionedStack(item, 5 + x * 18, 6 + y * 18, false);
                    stack.setMaxSize(1);
                    this.inputs.add(stack);
                }
            }
        }
        
        @Override
        public List<PositionedStack> getIngredients() {
            return this.getCycledIngredients(RecipeHandlerCarpenter.this.cycleticks / 20, this.inputs);
        }
        
        @Override
        public PositionedFluidTank getFluidTank() {
            return this.tank;
        }
        
        @Override
        public PositionedStack getResult() {
            return this.output;
        }
        
        public void generatePermutations() {
            for (PositionedStack p : this.inputs) {
                p.generatePermutations();
            }
        }
        
    }
    
    @Override
    public String getRecipeID() {
        return "forestry.carpenter";
    }
    
    @Override
    public String getRecipeName() {
        return Utils.translate("tile.for.factory.1.name", false);
    }
    
    @Override
    public String getGuiTexture() {
        return "forestry:textures/gui/carpenter.png";
    }
    
    @Override
    public void loadTransferRects() {
        this.addTransferRect(93, 36, 4, 18);
    }
    
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return guiClass;
    }
    
    @Override
    public void drawBackground(int recipe) {
        this.changeToGuiTexture();
        GuiDraw.drawTexturedModalRect(0, 0, 5, 14, 166, 65);
    }
    
    @Override
    public void drawExtras(int recipe) {
        this.drawProgressBar(93, 36, 176, 59, 4, 17, 80, 3);
    }
    
    @Override
    public void loadAllRecipes() {
        for (MachineCarpenter.Recipe recipe : MachineCarpenter.RecipeManager.recipes) {
            this.arecipes.add(new CachedCarpenterRecipe(recipe, true));
        }
    }
    
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (MachineCarpenter.Recipe recipe : MachineCarpenter.RecipeManager.recipes) {
            if (Utils.areStacksSameTypeCraftingSafe(recipe.getCraftingResult(), result)) {
                this.arecipes.add(new CachedCarpenterRecipe(recipe, true));
            }
        }
    }
    
    @Override
    public void loadUsageRecipes(ItemStack ingred) {
        super.loadUsageRecipes(ingred);
        for (MachineCarpenter.Recipe recipe : MachineCarpenter.RecipeManager.recipes) {
            CachedCarpenterRecipe crecipe = new CachedCarpenterRecipe(recipe);
            if (crecipe.inputs != null && crecipe.contains(crecipe.inputs, ingred)) {
                crecipe.generatePermutations();
                crecipe.setIngredientPermutation(crecipe.inputs, ingred);
                this.arecipes.add(crecipe);
            }
        }
    }
    
    @Override
    public void loadUsageRecipes(FluidStack ingredient) {
        for (MachineCarpenter.Recipe recipe : MachineCarpenter.RecipeManager.recipes) {
            if (Utils.areFluidsSameType(recipe.getLiquid(), ingredient)) {
                this.arecipes.add(new CachedCarpenterRecipe(recipe, true));
            }
        }
    }
    
}
