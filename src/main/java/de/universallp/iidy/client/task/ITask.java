package de.universallp.iidy.client.task;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * Created by universallp on 28.11.2016 15:37.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public interface ITask {

    void setTaskID(int taskID);

    int getTaskID();

    boolean needsUpdate(long worldTicks);

    void finish(TaskResult result);

    TaskResult isDone();

    BlockPos getPos();

    int getDim();

    TaskType getType();

    float getProgress();

    void setProgress(float f);

    String getOwnerUUID();

    String getFinishMessage();

    NBTTagCompound writeToNBT(NBTTagCompound tag);

    void readFromNBT(NBTTagCompound tag);

    enum TaskType {
        INVENTORY_SLOT("iidy.task.inventory"),
        BLOCK_STATE("iidy.task.blockstate"),
        NONE(""),
        DELETE("");

        String unlocalizedName;

        TaskType(String unlocalizedName) {
            this.unlocalizedName = unlocalizedName;
        }

        public String getLocalizedName() {
            return I18n.format(unlocalizedName);
        }
    }

    enum TaskResult {
        SUCCESS,
        FAILURE,
        UNFINISHED;

        public boolean toBoolean() {
            return this == FAILURE || this == SUCCESS;
        }
    }
}