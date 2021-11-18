package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.SectionExistException;
import nextstep.subway.exception.StationNotContainInUpOrDownStation;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();
    
    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
    
    public void updateSections(List<Section> sections) {
        this.sections = new Sections(sections);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public boolean hasStation(Station newStation) {
        return getStations().stream()
                .anyMatch(station -> station == newStation);
    }

    public void updateUpStation(Station upStation, Station downStation, int distance) {
        sections.updateUpStation(upStation, downStation, distance);
    }

    public void updateDownStation(Station upStation, Station downStation, int distance) {
        sections.updateDownStation(upStation, downStation, distance);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(new Section(this, upStation, downStation, distance));
    }

    public Line updateSection(Station upStation, Station downStation, int distance) {
        if (hasStation(upStation) && hasStation(downStation)) {
            throw new SectionExistException();
        }

        if (hasStation(upStation)) {
            updateUpStation(upStation, downStation, distance);
            addSection(upStation, downStation, distance);
            return this;
        }

        if (hasStation(downStation)) {
            updateDownStation(upStation, downStation, distance);
            addSection(upStation, downStation, distance);
            return this;
        }

        throw new StationNotContainInUpOrDownStation();
    }
}
