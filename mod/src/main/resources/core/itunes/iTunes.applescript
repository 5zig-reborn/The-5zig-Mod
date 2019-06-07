-- script to encode result to json format. Needed, because Apple messed up their own format when returning in console
-- using console because Apple messed up Java AppleScriptEngine eval: calling it for too long causes terrible Input lag
script json

    on encode(value)
        set type to class of value
        if type = text or type = integer or type = boolean then
            return value as text
        else if type = real then
            return replaceString(value as text, ",", ".")
        else if type = list then
            return encodeList(value)
        else if type = script then
            return value's toJson()
        else
            error "Unknown type " & type
        end
    end


    on encodeList(value_list)
        set out_list to {}
        repeat with value in value_list
            copy encode(value) to end of out_list
        end
        return "[" & join(out_list, ", ") & "]"
    end


    on encodeString(value)
        set rv to ""
        repeat with ch in value
            if id of ch = 34
                set quoted_ch to "\\\""
            else if id of ch = 92 then
                set quoted_ch to "\\\\"
            else if id of ch >= 32 and id of ch < 127
                set quoted_ch to ch
            else
                set quoted_ch to "\\u" & hex4(id of ch)
            end
            set rv to rv & quoted_ch
        end
        return "\"" & rv & "\""
    end


    on join(value_list, delimiter)
        set original_delimiter to AppleScript's text item delimiters
        set AppleScript's text item delimiters to delimiter
        set rv to value_list as text
        set AppleScript's text item delimiters to original_delimiter
        return rv
    end


    on hex4(n)
        set digit_list to "0123456789abcdef"
        set rv to ""
        repeat until length of rv = 4
            set digit to (n mod 16)
            set n to (n - digit) / 16 as integer
            set rv to (character (1+digit) of digit_list) & rv
        end
        return rv
    end


    on createDictWith(item_pairs)
        set item_list to {}

        script Dict
            on setkv(key, value)
                copy {key, value} to end of item_list
            end

            on toJson()
                set item_strings to {}
                repeat with kv in item_list
                    set key_str to encodeString(item 1 of kv)
                    set value_str to encode(item 2 of kv)
                    copy key_str & ": " & value_str to end of item_strings
                end
                return "{" & join(item_strings, ", ") & "}"
            end
        end

        repeat with pair in item_pairs
            Dict's setkv(item 1 of pair, item 2 of pair)
        end

        return Dict
    end


    on createDict()
        return createDictWith({})
    end

    on replaceString(the_text, search, replace)
        set AppleScript's text item delimiters to search
        set item_list to every text item of the_text
        set AppleScript's text item delimiters to replace
        set the_text to the item_list as string
        set AppleScript's text item delimiters to ""
        return the_text
    end replaceString

end script

-- checks if iTunes is running. Cannot use "running" because Apple messed that up as well and iTunes would launch afterwards
on itunes_running()
	tell application "System Events"
		set isRunning to ((application processes whose (name is equal to "iTunes")) count)
	end tell
	if isRunning is greater than 0 then
		return true
	else
		return false
	end if
end itunes_running

if not itunes_running() then
	return json's encode(json's createDictWith({{"running", false}}))
end if

-- hook into iTunes
tell application "iTunes"
	set current_state to player state
	if current_state is not stopped then
		set current_track to current track
		set last_position to player position
	else
		set current_track to null
		set last_position to 0
	end if
	-- wait until max_time (needs to be passed in at the top of the script) is over or until a status change happens
	if max_time is not -1 then
		repeat until player state is not current_state or (current_state is not stopped and current track is not current_track) or (current_state is not stopped and (player position -	last_position > 2 or player position - last_position < -2)) or max_time <= 0
			set max_time to max_time - 1
			set last_position to player position
			delay 1
		end repeat
	end if

	set playerState to player state
	if playerState is stopped then
		return json's encode(json's createDictWith({{"running", true}, {"playing", false}}))
	end if
	if playerState is playing then
		set isPlaying to true
	else
		set isPlaying to false
	end if

	set trackId to persistent ID of current track
	set trackName to json's encodeString(name of current track)
	set trackArtist to json's encodeString(artist of current track)
	set trackLength to duration of current track
	if artworks of current track is not {} then
	    set imageData to raw data of first artwork of current track
	    -- bug using, yep. thanks apple...
	    try
	        imageData as number
	    on error errormsg
	        set imageData to "\"" & text ((offset of "«" in errormsg) + 10) thru ((offset of "»" in errormsg) - 1) of errormsg & "\""
	    end try
	else
	    set imageData to "null"
	end if

	return json's encode(json's createDictWith({{"running", true}, {"playing", isPlaying}, {"track", json's createDictWith({{"id", trackId}, {"name", trackName}, {"artist", trackArtist}, {"length", trackLength}, {"artwork", imageData}})}, {"playingPosition", player position}}))
end tell