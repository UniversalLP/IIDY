package de.universallp.iidy.core.handler;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.client.task.InventoryTask;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by universal on 19.12.2016 17:43.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/IIDY
 */
public class TaskSaveHandler extends WorldSavedData
{
    private static final String DATA_NAME = IsItDoneYet.MODID + "-TasksData";

    private Map<String, List<ITask>> activeTasks = new HashMap<String, List<ITask>>();

    public TaskSaveHandler() {
        super(DATA_NAME);
    }

    public TaskSaveHandler(String name) { super(name); } // Must stay

    public Map<String, List<ITask>> getActiveTasks() {
        return activeTasks;
    }

    public static TaskSaveHandler get(World w) {
        MapStorage storage =  w.getMapStorage();

        TaskSaveHandler instance = (TaskSaveHandler) storage.getOrLoadData(TaskSaveHandler.class, DATA_NAME);
        if (instance == null) {
            instance = new TaskSaveHandler();
            storage.setData(DATA_NAME, instance);
            instance.init();
        }
        return instance;
    }

    public void init() {
        if (activeTasks == null)
            activeTasks = new HashMap<>();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt == null)
            return;

        if (nbt.hasKey("IIDY-Players")) {
            IsItDoneYet.proxy.log.debug("Starting task loading...");

            NBTTagList playerList = nbt.getTagList("IIDY-Players", 10);
            NBTTagList playerTasks;
            NBTTagCompound taskNBT;
            NBTTagCompound listCompound;
            activeTasks = new HashMap<>();
            List<ITask> taskList = new ArrayList<ITask>();
            ITask tempTask = null;

            for (int i = 0; i < playerList.tagCount(); i++) {
                listCompound = playerList.getCompoundTagAt(i);

                if (!listCompound.hasKey("IIDY-Tasks"))
                    continue;

                playerTasks = listCompound.getTagList("IIDY-Tasks", 10);
                taskList.clear();

                if (playerTasks.tagCount() <= 0)
                    continue;

                for (int b = 0; b < playerTasks.tagCount(); b++) {
                    taskNBT = playerTasks.getCompoundTagAt(i);

                    if (!taskNBT.hasKey("TaskType"))
                        continue;

                    ITask.TaskType type = ITask.TaskType.values()[taskNBT.getByte("TaskType")];

                    switch (type) {
                        case INVENTORY_SLOT:
                            tempTask = new InventoryTask();
                            tempTask.readFromNBT(taskNBT);
                            break;
                        case BLOCK_STATE:
                            break;
                    }

                    taskList.add(tempTask);
                }

                if (taskList.size() <= 0)
                    break;

                activeTasks.put(taskList.get(0).getOwnerUUID(), taskList);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList taskList = new NBTTagList();
        NBTTagList players  = new NBTTagList();
        NBTTagCompound listCompound;
        FMLLog.log(IsItDoneYet.MODID, Level.INFO, "[IIDY] Starting task saving...");

        for (String player : activeTasks.keySet()) {
            taskList = new NBTTagList();
            listCompound = new NBTTagCompound();

            for (ITask t : activeTasks.get(player)) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                nbtTagCompound = t.writeToNBT(nbtTagCompound);
                nbtTagCompound.setByte("TaskType", (byte) t.getType().ordinal());
                taskList.appendTag(nbtTagCompound);
            }

            listCompound.setTag("IIDY-Tasks", taskList);
            players.appendTag(listCompound);
        }

        if (taskList.tagCount() > 0)
            compound.setTag("IIDY-Players", players);
        return compound;
    }
}

