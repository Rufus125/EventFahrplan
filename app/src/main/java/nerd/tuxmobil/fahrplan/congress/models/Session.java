package nerd.tuxmobil.fahrplan.congress.models;

import static java.util.Collections.emptyList;
import static info.metadude.android.eventfahrplan.commons.temporal.Moment.MILLISECONDS_OF_ONE_MINUTE;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.ObjectsCompat;

import org.threeten.bp.ZoneOffset;

import java.util.List;

import info.metadude.android.eventfahrplan.commons.temporal.DateFormatter;
import info.metadude.android.eventfahrplan.commons.temporal.Moment;
import info.metadude.android.eventfahrplan.network.serialization.FahrplanParser;
import info.metadude.android.eventfahrplan.network.temporal.DateParser;
import nerd.tuxmobil.fahrplan.congress.R;
import nerd.tuxmobil.fahrplan.congress.repositories.SessionsTransformer;
import nerd.tuxmobil.fahrplan.congress.schedule.Conference;

/**
 * Application model representing a lecture, a workshop or any similar time-framed happening.
 */
public class Session {

    public String title;
    public String subtitle;
    @Nullable
    public String feedbackUrl;          // URL to Frab/Pretalx feedback system, e.g. feedbackUrl = "https://talks.event.net/2023/talk/V8LUNA/feedback"
    public String url;
    public int day;                     // XML values start with 1
    public String date;                 // YYYY-MM-DD
    public long dateUTC;                // milliseconds
    @Nullable
    public ZoneOffset timeZoneOffset;
    public int startTime;               // minutes since day start
    public int relStartTime;            // minutes since conference start
    public int duration;                // minutes

    public String roomName;
    public String roomIdentifier;       // Unique identifier of a room, e.g. "bccb6a5b-b26b-4f17-90b9-b5966f5e34d8"

    /**
     * The value of this field is generated by {@link FahrplanParser} when parsing the schedule. It contributes to how
     * the rooms are sorted in the user interface, see {@link SessionsTransformer}. But it should not be used by any
     * other code!
     */
    @Deprecated
    public int roomIndex;

    public List<String> speakers;
    public String track;
    public String sessionId;
    public String type;
    public String lang;
    public String slug;
    public String abstractt;
    public String description;

    /**
     * Comma separated Markdown formatted links, see ParserTask#parseFahrplan.
     */
    public String links;

    public boolean highlight;
    public boolean hasAlarm;

    public String recordingLicense;
    public boolean recordingOptOut;

    public boolean changedTitle;
    public boolean changedSubtitle;
    public boolean changedRoomName;
    public boolean changedDay;
    public boolean changedTime;
    public boolean changedDuration;
    public boolean changedSpeakers;
    public boolean changedRecordingOptOut;
    public boolean changedLanguage;
    public boolean changedTrack;
    public boolean changedIsNew;
    public boolean changedIsCanceled;

    private static final boolean RECORDING_OPTOUT_OFF = false;

    public Session(String sessionId) {
        title = "";
        subtitle = "";
        feedbackUrl = null;
        url = "";
        day = 0;
        roomName = "";
        roomIdentifier = "";
        slug = "";
        startTime = 0;
        duration = 0;
        speakers = emptyList();
        track = "";
        type = "";
        lang = "";
        abstractt = "";
        description = "";
        relStartTime = 0;
        links = "";
        date = "";
        this.sessionId = sessionId;
        highlight = false;
        hasAlarm = false;
        dateUTC = 0;
        timeZoneOffset = null;
        roomIndex = 0;
        recordingLicense = "";
        recordingOptOut = RECORDING_OPTOUT_OFF;
        changedTitle = false;
        changedSubtitle = false;
        changedRoomName = false;
        changedDay = false;
        changedSpeakers = false;
        changedRecordingOptOut = false;
        changedLanguage = false;
        changedTrack = false;
        changedIsNew = false;
        changedTime = false;
        changedDuration = false;
        changedIsCanceled = false;
    }

    public Session(@NonNull Session session) {
        this.title = session.title;
        this.subtitle = session.subtitle;
        this.feedbackUrl = session.feedbackUrl;
        this.url = session.url;
        this.day = session.day;
        this.date = session.date;
        this.dateUTC = session.dateUTC;
        this.timeZoneOffset = session.timeZoneOffset;
        this.startTime = session.startTime;
        this.relStartTime = session.relStartTime;
        this.duration = session.duration;
        this.roomName = session.roomName;
        this.roomIdentifier = session.roomIdentifier;
        this.roomIndex = session.roomIndex;
        this.speakers = session.speakers;
        this.track = session.track;
        this.sessionId = session.sessionId;
        this.type = session.type;
        this.lang = session.lang;
        this.slug = session.slug;
        this.abstractt = session.abstractt;
        this.description = session.description;
        this.links = session.links;
        this.highlight = session.highlight;
        this.hasAlarm = session.hasAlarm;
        this.recordingLicense = session.recordingLicense;
        this.recordingOptOut = session.recordingOptOut;

        this.changedTitle = session.changedTitle;
        this.changedSubtitle = session.changedSubtitle;
        this.changedRoomName = session.changedRoomName;
        this.changedDay = session.changedDay;
        this.changedTime = session.changedTime;
        this.changedDuration = session.changedDuration;
        this.changedSpeakers = session.changedSpeakers;
        this.changedRecordingOptOut = session.changedRecordingOptOut;
        this.changedLanguage = session.changedLanguage;
        this.changedTrack = session.changedTrack;
        this.changedIsNew = session.changedIsNew;
        this.changedIsCanceled = session.changedIsCanceled;
    }

    @NonNull
    public String getLinks() {
        return links == null ? "" : links;
    }

    @VisibleForTesting
    public Moment getStartTimeMoment() {
        long startOfDayTimestamp = DateParser.getDateTime(date);
        return Moment.ofEpochMilli(startOfDayTimestamp).plusMinutes(relStartTime);
    }

    /**
     * Returns the start time in milliseconds.
     * <p>
     * The {@link #dateUTC} is given precedence if its value is bigger then `0`.
     * Otherwise the start time is determined based on {@link #getStartTimeMoment}.
     */
    public long getStartTimeMilliseconds() {
        return (dateUTC > 0) ? dateUTC : getStartTimeMoment().toMilliseconds();
    }

    /**
     * Returns a moment based on the start time milliseconds.
     * <p/>
     * Don't use in {@link Conference.Companion#ofSessions)} as long as {@link #relStartTime} is supported.
     * See: <a href="https://github.com/EventFahrplan/EventFahrplan/commit/5a4022b00434700274a824cc63f6d54a18b06fac">5a402</a>
     */
    public Moment getStartsAt() {
        if (dateUTC <= 0) {
            throw new IllegalArgumentException("Field 'dateUTC' must be more than 0.");
        }
        return Moment.ofEpochMilli(dateUTC);
    }

    /**
     * Returns a moment based on summing up the start time milliseconds and the duration.
     */
    @NonNull
    public Moment getEndsAt() {
        return Moment.ofEpochMilli(dateUTC + (long) duration * MILLISECONDS_OF_ONE_MINUTE);
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (!ObjectsCompat.equals(feedbackUrl, session.feedbackUrl)) return false;
        if (day != session.day) return false;
        if (duration != session.duration) return false;
        if (recordingOptOut != session.recordingOptOut) return false;
        if (startTime != session.startTime) return false;
        if (!ObjectsCompat.equals(date, session.date)) return false;
        if (!ObjectsCompat.equals(lang, session.lang)) return false;
        if (!sessionId.equals(session.sessionId)) return false;
        if (!ObjectsCompat.equals(recordingLicense, session.recordingLicense)) return false;
        if (!ObjectsCompat.equals(roomName, session.roomName)) return false;
        if (!ObjectsCompat.equals(roomIdentifier, session.roomIdentifier)) return false;
        if (!ObjectsCompat.equals(speakers, session.speakers)) return false;
        if (!ObjectsCompat.equals(subtitle, session.subtitle)) return false;
        if (!title.equals(session.title)) return false;
        if (!ObjectsCompat.equals(track, session.track)) return false;
        if (!ObjectsCompat.equals(type, session.type)) return false;
        if (dateUTC != session.dateUTC) return false;
        if (!ObjectsCompat.equals(timeZoneOffset, session.timeZoneOffset)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + ObjectsCompat.hashCode(subtitle);
        result = 31 * result + ObjectsCompat.hashCode(feedbackUrl);
        result = 31 * result + day;
        result = 31 * result + ObjectsCompat.hashCode(roomName);
        result = 31 * result + ObjectsCompat.hashCode(roomIdentifier);
        result = 31 * result + startTime;
        result = 31 * result + duration;
        result = 31 * result + ObjectsCompat.hashCode(speakers);
        result = 31 * result + ObjectsCompat.hashCode(track);
        result = 31 * result + sessionId.hashCode();
        result = 31 * result + ObjectsCompat.hashCode(type);
        result = 31 * result + ObjectsCompat.hashCode(lang);
        result = 31 * result + ObjectsCompat.hashCode(date);
        result = 31 * result + ObjectsCompat.hashCode(recordingLicense);
        result = 31 * result + (recordingOptOut ? 1 : 0);
        result = 31 * result + (int) dateUTC;
        result = 31 * result + ObjectsCompat.hashCode(timeZoneOffset);
        return result;
    }

    public void cancel() {
        changedIsCanceled = true;
        changedTitle = false;
        changedSubtitle = false;
        changedRoomName = false;
        changedDay = false;
        changedSpeakers = false;
        changedRecordingOptOut = false;
        changedLanguage = false;
        changedTrack = false;
        changedIsNew = false;
        changedTime = false;
        changedDuration = false;
    }

    public String getChangedStateString() {
        return "Session{" +
                "changedTitle=" + changedTitle +
                ", changedSubtitle=" + changedSubtitle +
                ", changedRoomName=" + changedRoomName +
                ", changedDay=" + changedDay +
                ", changedTime=" + changedTime +
                ", changedDuration=" + changedDuration +
                ", changedSpeakers=" + changedSpeakers +
                ", changedRecordingOptOut=" + changedRecordingOptOut +
                ", changedLanguage=" + changedLanguage +
                ", changedTrack=" + changedTrack +
                ", changedIsNew=" + changedIsNew +
                ", changedIsCanceled=" + changedIsCanceled +
                '}';
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isChanged() {
        return changedDay || changedDuration ||
                changedLanguage || changedRecordingOptOut ||
                changedRoomName || changedSpeakers || changedSubtitle ||
                changedTime || changedTitle || changedTrack;
    }

    @NonNull
    public static String getDurationContentDescription(@NonNull Context context, int duration) {
        return context.getString(R.string.session_list_item_duration_content_description, duration);
    }

    @NonNull
    public static String getTitleContentDescription(@NonNull Context context, @NonNull String title) {
        return TextUtils.isEmpty(title) ? "" : context.getString(R.string.session_list_item_title_content_description, title);
    }

    @NonNull
    public static String getSubtitleContentDescription(@NonNull Context context, @NonNull String subtitle) {
        return TextUtils.isEmpty(subtitle) ? "" : context.getString(R.string.session_list_item_subtitle_content_description, subtitle);
    }

    @NonNull
    public String getFormattedSpeakers() {
        return speakers == null ? "" : TextUtils.join(", ", speakers);
    }

    @NonNull
    public String getLanguageText() {
        if (TextUtils.isEmpty(lang)) {
            return "";
        } else {
            return lang
                    .replace("-formal", "")
                    .replace("German", "de")
                    .replace("german", "de")
                    .replace("Deutsch", "de")
                    .replace("deutsch", "de")
                    .replace("English", "en")
                    .replace("english", "en")
                    .replace("Englisch", "en")
                    .replace("englisch", "en")
                    ;
        }
    }

    public String getFormattedTrackLanguageText() {
        StringBuilder builder = new StringBuilder();
        builder.append(track);
        if (!TextUtils.isEmpty(lang)) {
            String language = getLanguageText();
            builder.append(" [").append(language).append("]");
        }
        return builder.toString();
    }

    @NonNull
    public static String getRoomNameContentDescription(@NonNull Context context, @NonNull String roomName) {
        return context.getString(R.string.session_list_item_room_content_description, roomName);
    }

    @NonNull
    public static String getSpeakersContentDescription(@NonNull Context context, int speakersCount, @NonNull String formattedSpeakerNames) {
        return context.getResources().getQuantityString(R.plurals.session_list_item_speakers_content_description, speakersCount, formattedSpeakerNames);
    }

    @NonNull
    public static String getFormattedTrackContentDescription(@NonNull Context context, @NonNull String trackName, @NonNull String languageCode) {
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.session_list_item_track_content_description, trackName));
        if (!TextUtils.isEmpty(languageCode)) {
            builder.append("; ").append(getLanguageContentDescription(context, languageCode));
        }
        return builder.toString();
    }

    @NonNull
    public static String getLanguageContentDescription(@NonNull Context context, @NonNull String languageCode) {
        if (TextUtils.isEmpty(languageCode)) {
            return context.getString(R.string.session_list_item_language_unknown_content_description);
        }
        String languageName;
        switch (languageCode) {
            case "en":
                languageName = context.getString(R.string.session_list_item_language_english_content_description);
                break;
            case "de":
                languageName = context.getString(R.string.session_list_item_language_german_content_description);
                break;
            case "pt":
                languageName = context.getString(R.string.session_list_item_language_portuguese_content_description);
                break;
            default:
                languageName = languageCode;
        }
        return context.getString(R.string.session_list_item_language_content_description, languageName);
    }

    @NonNull
    public static String getStartTimeContentDescription(@NonNull Context context, @NonNull String startTimeText) {
        return context.getString(R.string.session_list_item_start_time_content_description, startTimeText);
    }

    @NonNull
    public static String getHighlightContentDescription(@NonNull Context context, boolean isHighlighted) {
        int stringResource = isHighlighted ? R.string.session_list_item_favored_content_description : R.string.session_list_item_not_favored_content_description;
        return context.getString(stringResource);
    }

    @NonNull
    public static String getStateContentDescription(@NonNull Context context, @NonNull Session session, Boolean useDeviceTimeZone) {
        String roomNameContentDescription = getRoomNameContentDescription(context, session.roomName);
        String startsAtText = DateFormatter.newInstance(useDeviceTimeZone).getFormattedTime(session.dateUTC, session.timeZoneOffset);
        String startsAtContentDescription = getStartTimeContentDescription(context, startsAtText);
        String isHighlightContentDescription = getHighlightContentDescription(context, session.highlight);
        return isHighlightContentDescription + ", " + startsAtContentDescription + ", " + roomNameContentDescription;
    }

    public void shiftRoomIndexBy(int amount) {
        roomIndex += amount;
    }

}
