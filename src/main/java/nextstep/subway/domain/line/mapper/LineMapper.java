package nextstep.subway.domain.line.mapper;

import nextstep.subway.domain.line.domain.Line;
import nextstep.subway.domain.line.dto.LineResponse;
import nextstep.subway.domain.section.domain.Section;

import java.util.ArrayList;
import java.util.List;

public class LineMapper {

    private LineMapper() {
    }

    public static List<LineResponse.LineStation> sectionToLineStation(final Line line) {
        final List<Section> sections = line.getSections();
        List<LineResponse.LineStation> lineStations = new ArrayList<>();
        for (Section section : sections) {
            LineResponse.LineStation station = new LineResponse.LineStation(section.getId(), section.getStation().getName(), section.getCreatedDate(), section.getModifiedDate());
            lineStations.add(station);
        }

        return lineStations;
    }
}