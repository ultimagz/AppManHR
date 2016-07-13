package com.appman.intern;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppBackupAgent extends BackupAgent {
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
//        FileInputStream instream = new FileInputStream(oldState.getFileDescriptor());
//        DataInputStream in = new DataInputStream(instream);
//
//        try {
//            // Get the last modified timestamp from the state file and data file
//            long stateModified = in.readLong();
//            long fileModified = mDataFile.lastModified();
//
//            if (stateModified != fileModified) {
//                // The file has been modified, so do a backup
//                // Or the time on the device changed, so be safe and do a backup
//            } else {
//                // Don't back up because the file hasn't changed
//                return;
//            }
//        } catch (IOException e) {
//            // Unable to read state file... be safe and do a backup
//        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
//        // There should be only one entity, but the safest
//        // way to consume it is using a while loop
//        while (data.readNextHeader()) {
//            String key = data.getKey();
//            int dataSize = data.getDataSize();
//
//            // If the key is ours (for saving top score). Note this key was used when
//            // we wrote the backup entity header
//            if (TOPSCORE_BACKUP_KEY.equals(key)) {
//                // Create an input stream for the BackupDataInput
//                byte[] dataBuf = new byte[dataSize];
//                data.readEntityData(dataBuf, 0, dataSize);
//                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
//                DataInputStream in = new DataInputStream(baStream);
//
//                // Read the player name and score from the backup data
//                mPlayerName = in.readUTF();
//                mPlayerScore = in.readInt();
//
//                // Record the score on the device (to a file or something)
//                recordScore(mPlayerName, mPlayerScore);
//            } else {
//                // We don't know this entity key. Skip it. (Shouldn't happen.)
//                data.skipEntityData();
//            }
//        }
//
//        // Finally, write to the state blob (newState) that describes the restored data
//        FileOutputStream outstream = new FileOutputStream(newState.getFileDescriptor());
//        DataOutputStream out = new DataOutputStream(outstream);
//        out.writeUTF(mPlayerName);
//        out.writeInt(mPlayerScore);
    }
}
