package fr.narrnouille.customnpcsextendedcompat.mixin.customnpcs;

import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "noppes.npcs.api.wrapper.ScoreboardWrapper")
public abstract class ScoreboardWrapperMixin {
    @Shadow
    private Scoreboard board;

    @Shadow
    private Objective getObjectiveWithException(String objective) {
        throw new AssertionError();
    }

    /**
     * @author Narrnouille
     * @reason CustomNPCs resolves the score holder through getPlayerByName(...), which returns null for
     * offline or fake scoreboard names and then crashes vanilla scoreboard code.
     */
    @Overwrite
    public void setPlayerScore(String player, String objective, int score) {
        Objective objec = this.getObjectiveWithException(objective);
        if (objec.getCriteria().isReadOnly()) {
            return;
        }

        ScoreAccess sco = this.board.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player), objec);
        sco.set(score);
    }

    /**
     * @author Narrnouille
     * @reason Keep CustomNPCs scoreboard script API usable for offline or fake scoreboard names.
     */
    @Overwrite
    public int getPlayerScore(String player, String objective) {
        Objective objec = this.getObjectiveWithException(objective);
        if (objec.getCriteria().isReadOnly()) {
            return 0;
        }

        return this.board.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player), objec).get();
    }

    /**
     * @author Narrnouille
     * @reason Keep CustomNPCs scoreboard script API usable for offline or fake scoreboard names.
     */
    @Overwrite
    public boolean hasPlayerObjective(String player, String objective) {
        Objective objec = this.getObjectiveWithException(objective);
        return this.board.getPlayerScoreInfo(ScoreHolder.forNameOnly(player), objec) != null;
    }

    /**
     * @author Narrnouille
     * @reason CustomNPCs removes the player from their team instead of deleting the requested score.
     */
    @Overwrite
    public void deletePlayerScore(String player, String objective) {
        Objective objec = this.getObjectiveWithException(objective);
        this.board.resetSinglePlayerScore(ScoreHolder.forNameOnly(player), objec);
    }
}
