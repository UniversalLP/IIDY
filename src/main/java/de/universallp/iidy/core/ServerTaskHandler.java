package de.universallp.iidy.core;

import de.universallp.iidy.IsItDoneYet;
import de.universallp.iidy.client.task.ITask;
import de.universallp.iidy.core.handler.TaskSaveHandler;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by universallp on 01.12.2016 14:24.
 * This file is part of IIDY which is licenced
 * under the MOZILLA PUBLIC LICENSE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/UniversalLP/IIDY
 */
public class ServerTaskHandler {
    private TaskSaveHandler taskSaves;
    private MinecraftServer server;
    private int nextTaskID = 0;
    public boolean CAN_SAVE;
    public boolean INITIALIZED = false;

    public ServerTaskHandler() {
        CAN_SAVE = true;
    }

    public void addTask(ITask t) {
        t.setTaskID(nextTaskID);
        if (taskSaves.getActiveTasks().containsKey(t.getOwnerUUID())) {
            List<ITask> currentTasks = taskSaves.getActiveTasks().get(t.getOwnerUUID());
            currentTasks.add(t);
            taskSaves.getActiveTasks().put(t.getOwnerUUID(), currentTasks);
        } else {
            List<ITask> currentTasks = new ArrayList<ITask>();
            currentTasks.add(t);

            taskSaves.getActiveTasks().put(t.getOwnerUUID(), currentTasks);
        }
        taskSaves.markDirty();
        nextTaskID++;
    }

    public void tryRemoveTask(int taskID, String playerID) {
        if (!INITIALIZED)
            return;
        List<ITask> playerTasks = taskSaves.getActiveTasks().get(playerID);

        if (playerTasks != null && playerTasks.size() > 0) {
            int i = 0;
            for (ITask t : playerTasks) {
                if (t.getTaskID() == taskID) {
                    break;
                }
                i++;
            }
            playerTasks.remove(i);
            taskSaves.getActiveTasks().put(playerID, playerTasks);
            taskSaves.markDirty();
        }
    }

    public List<ITask> getTasksForPlayer(String uuid) {
        return taskSaves.getActiveTasks().containsKey(uuid) ? taskSaves.getActiveTasks().get(uuid) : null;
    }

    public boolean isIdle() {
        return INITIALIZED && taskSaves.getActiveTasks().size() == 0;
    }

    public void updateTasks() {
        if (!INITIALIZED)
            return;

        List<ITask> finishedTasks = new ArrayList<ITask>();
        List<ITask> currentTasks;
        ITask.TaskResult result;

        for (String player : taskSaves.getActiveTasks().keySet()) {
            if (server.isDedicatedServer() && server.getPlayerList().getPlayerByUUID(UUID.fromString(player)) == null) // Logged out players don't get updates
                continue;

            currentTasks = taskSaves.getActiveTasks().get(player);
            for (ITask task : currentTasks) {
                if (task.needsUpdate(IsItDoneYet.proxy.getWorldFromDimension(task.getDim()).getWorldTime())) {
                    result = task.isDone();
                    if (result.toBoolean()) {
                        finishedTasks.add(task);
                        task.finish(result);
                    }
                }
            }

            currentTasks.removeAll(finishedTasks);
            finishedTasks.clear();

            if (currentTasks.size() > 0)
                taskSaves.getActiveTasks().put(player, currentTasks);
            else
                taskSaves.getActiveTasks().remove(player);
        }

    }

    public void setServer(MinecraftServer server) {
        this.server = server;
        taskSaves = TaskSaveHandler.get(server.getEntityWorld());
        INITIALIZED = true;
    }
}
