package nextstep.subway.line.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.Stations;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private final List<Section> sections;

    public Sections() {
        sections = new ArrayList<>();
    }

    public void add(Section section) {
        sections.add(section);
    }

    public Station findFirstUpStation() {
        Stations downStations = new Stations(findDownStations());
        return sections.stream().filter(
                section -> !downStations.isIn(section.getUpStation()))
                .findFirst()
                .orElseThrow(NotFoundStationException::new)
                .getUpStation();
    }

    private List<Station> findDownStations() {
        return sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }

    public Stations getStations() {
        Station findStation = findFirstUpStation();
        List<Station> sectionStationList = getSectionStationList(findStation);
        return new Stations(sectionStationList);
    }

    private List<Station> getSectionStationList(Station station){
        List<Station> stations = new ArrayList<>();
        while(!Objects.isNull(station)) {
            stations.add(station);
            Section nextSection = findNextSection(station);
            station = nextSection.getDownStation();
        }
        return stations;
    }

    private Section findNextSection(final Station findStation) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(findStation))
                .findFirst()
                .orElse(new Section());
    }

    public List<Section> getSections() {
        return new ArrayList<>(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}