/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on HikingApp QuizQuestion class
 */

package ch.epfl.sweng.team7.database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the data in a quiz question returned by the SwEng server.
 *
 */
public class TrackData {
    // TODO implement
    private int mTrackId;   // Database track ID of this track
    private int mOwnerId;   // Database user ID of owner
    private long mDate;     // A UTC time stamp
    private List<TrackPoint> trackPoints;   // Points of the track, in chronological order

    // TODO remove
    private long mId;
    private String mOwner;
    private String mBody;
    private List<String> mAnswers;
    private int mSolutionIndex;
    private List<String> mTags;
    /**
     * Creates a new QuizQuestion instance from the question elements provided
     * as arguments.
     * @param id the numeric ID of the question
     * @param owner the name of the owner of the question
     * @param body the question body
     * @param answers a list of two or more possible question answers
     * @param solutionIndex the index in the answer list of the correct answer
     * @param tags a list of zero or more tags associated to the question
     */
    public TrackData(long id, String owner, String body, List<String> answers,
            int solutionIndex, List<String> tags) {
        if (owner == null) {
            throw new NullPointerException("Owner is null");
        }
        if (body == null) {
            throw new NullPointerException("Body is null");
        }
        if (answers.size() < 2) {
            throw new IllegalArgumentException("Answer list must be size two or more");
        }
        for (String answer: answers) {
            if (answer == null) {
                throw new NullPointerException("Answer is null");
            }
        }
        if (solutionIndex < 0 || solutionIndex >= answers.size()) {
            throw new IllegalArgumentException("Invalid solutionIndex value");
        }
        for (String tag: tags) {
            if (tag == null) {
                throw new NullPointerException("Tag is null");
            }
        }
        
        mId = id;
        mOwner = owner;
        mBody = body;
        mAnswers = new ArrayList<String>(answers);
        mSolutionIndex = solutionIndex;
        mTags = new ArrayList<String>(tags);
    }
    
    /**
     * Returns the question ID.
     */
    public long getID() {
        return mId;
    }
    
    /**
     * Returns the question owner.
     */
    public String getOwner() {
        return mOwner;
    }
    
    /**
     * Returns the question body.
     */
    public String getBody() {
        return mBody;
    }
    
    /**
     * Returns a list of the question answers.
     */
    public List<String> getAnswers() {
        return new ArrayList<String>(mAnswers);
    }
    
    /**
     * Returns the index of the solution in the answer list.
     */
    public int getSolutionIndex() {
        return mSolutionIndex;
    }
    
    /**
     * Returns a (possibly empty) list of question tags.
     */
    public List<String> getTags() {
        return new ArrayList<String>(mTags);
    }

    /**
     * Creates a new QuizQuestion object by parsing a JSON object in the format
     * returned by the quiz server.
     * @param jsonObject a {@link JSONObject} encoding.
     * @return a new QuizQuestion object.
     * @throws JSONException in case of malformed JSON.
     */
    public static TrackData parseFromJSON(JSONObject jsonObject) throws JSONException {

        // Check that Strings are correct.
        if (!(jsonObject.get("question") instanceof String) ||
                !(jsonObject.get("owner") instanceof String)) {
            throw new JSONException("Invalid question structure");
        }

        JSONArray jsonAnswers = jsonObject.getJSONArray("answers");
        List<String> answers = new ArrayList<String>();
        for (int i = 0; i < jsonAnswers.length(); ++i) {
            // Check that Strings are correct.
            if (!(jsonAnswers.get(i) instanceof String)) {
                throw new JSONException("Invalid question structure");
            }
            answers.add(jsonAnswers.getString(i));
        }

        JSONArray jsonTags = jsonObject.getJSONArray("tags");
        List<String> tags = new ArrayList<String>();
        for (int i = 0; i < jsonTags.length(); ++i) {
            if (!(jsonTags.get(i) instanceof String)) {
                throw new JSONException("Invalid question structure");
            }
            tags.add(jsonTags.getString(i));
        }

        try {
            return new TrackData(
                    jsonObject.getLong("id"),
                    jsonObject.getString("owner"),
                    jsonObject.getString("question"),
                    answers,
                    jsonObject.getInt("solutionIndex"),
                    tags);
        } catch (IllegalArgumentException e) {
            throw new JSONException("Invalid question structure");
        } catch (NullPointerException e) {
            throw new JSONException("Invalid question structure");
        }
    }
}
