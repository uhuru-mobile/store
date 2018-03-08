/*
 * Copyright (C) 2014 Hamon Valentin <vhamon@et.esiea-ouest.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package fdroid.fdroid.org.uhurustore;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class AndroidShell {

    private static final String LOG_TAG = "uhuru-store";

    public static String execSh (String command) {

        String line = "";
        String output = "";

        if(command.endsWith("\n")) {
            command = command.substring(0, command.length()-1);
        }

        command = command + "; echo __end\n";

        try {
            // Passe root et exec la commande
            Process p = Runtime.getRuntime().exec("sh");
            Log.v(LOG_TAG,"sh --\n");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes(command);

            Log.v(LOG_TAG,"cmd: " + command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            line = reader.readLine();
            while(!line.startsWith("__end") && line != null) {
                Log.v("UHURU-STORE","line :" + line);
                output = output + line + "\n";
                line = reader.readLine();
            }

            reader.close();
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            p.waitFor();
            p.destroy();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return output;
    }
}
