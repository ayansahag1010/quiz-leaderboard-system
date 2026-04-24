import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String regNo = "RA2311003010161";

        // store total scores
        Map<String, Integer> scoreMap = new HashMap<>();

        // track processed events (to avoid duplicates)
        Set<String> seen = new HashSet<>();

        for (int poll = 0; poll < 10; poll++) {

            System.out.println("Fetching poll " + poll + "...");

            String api = "https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/messages"
                    + "?regNo=" + regNo + "&poll=" + poll;

            URL url = new URL(api);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            StringBuilder res = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                res.append(line);
            }

            JSONObject json = new JSONObject(res.toString());
            JSONArray events = json.getJSONArray("events");

            for (int i = 0; i < events.length(); i++) {

                JSONObject obj = events.getJSONObject(i);

                String roundId = obj.getString("roundId");
                String name = obj.getString("participant");
                int score = obj.getInt("score");

                // unique key
                String key = roundId + "_" + name;

                // skip duplicates
                if (seen.contains(key)) continue;

                seen.add(key);

                // add score
                int prev = scoreMap.getOrDefault(name, 0);
                scoreMap.put(name, prev + score);
            }

            // mandatory delay
            Thread.sleep(5000);
        }

        // sort leaderboard
        List<Map.Entry<String, Integer>> leaderboard =
                new ArrayList<>(scoreMap.entrySet());

        leaderboard.sort((a, b) -> b.getValue() - a.getValue());

        int total = 0;

        System.out.println("\nLeaderboard:");
        for (Map.Entry<String, Integer> entry : leaderboard) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            total += entry.getValue();
        }

        System.out.println("Total Score = " + total);

        URL postUrl = new URL("https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit");
        HttpURLConnection postCon = (HttpURLConnection) postUrl.openConnection();

        postCon.setRequestMethod("POST");
        postCon.setRequestProperty("Content-Type", "application/json");
        postCon.setDoOutput(true);

        JSONArray arr = new JSONArray();

        for (Map.Entry<String, Integer> entry : leaderboard) {
            JSONObject obj = new JSONObject();
            obj.put("participant", entry.getKey());
            obj.put("totalScore", entry.getValue());
            arr.put(obj);
        }

        JSONObject body = new JSONObject();
        body.put("regNo", regNo);
        body.put("leaderboard", arr);

        OutputStream os = postCon.getOutputStream();
        os.write(body.toString().getBytes());
        os.flush();

        BufferedReader response = new BufferedReader(
                new InputStreamReader(postCon.getInputStream()));

        String out;
        System.out.println("\nSubmission Response:");
        while ((out = response.readLine()) != null) {
            System.out.println(out);
        }
    }
}