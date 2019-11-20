package divinerpg.objects.entities.entity.vethea;

import divinerpg.objects.entities.entity.EntityGifterNPC;
import divinerpg.registry.ModItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MysteriousManLayer1 extends EntityGifterNPC {

    public MysteriousManLayer1(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.32D);
    }

    @Override
    protected ItemStack getGift() {
        return new ItemStack(ModItems.teakerLump, 3);
    }

    @Override
    protected String[] getMessages() {
        String[] messages = { "message.mysterious_man_layer_1.1", "message.mysterious_man_layer_1.2", "message.mysterious_man_layer_1.3" };
        return messages;
    }

    @Override
    protected String getTranslationName() {
        return "entity.divinerpg.mysterious_man_layer_1.name";
    }

    public int getSpawnLayer() {
        return 1;
    }

    @Override
    public boolean getCanSpawnHere() {
        int spawnLayer = this.getSpawnLayer();

        if(spawnLayer == 0) {
            return super.getCanSpawnHere();
        }
        else {
            return this.posY < 48.0D * spawnLayer  && this.posY > 48.0D * (spawnLayer - 1) && super.getCanSpawnHere();
        }
    }
}
