package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Station downStation;

    @Embedded
    private Distance distance;

    public Section() { }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.valueOf(distance);
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public boolean isInFrontOf(Section section) {
        return this.downStation.compareName(section.upStation());
    }

    public boolean isInMidFrontOf(Section section) {
        return this.upStation.compareName(section.upStation());
    }

    public boolean isInMidRearOf(Section section) {
        return this.downStation.compareName(section.downStation());
    }

    public boolean isBehindOf(Section section) {
        return this.upStation.compareName(section.downStation());
    }

    public void connectBehindOf(Section section) {
        shortenDistanceUsing(section);
        this.upStation = section.downStation();
    }

    public void connectInFrontOf(Section section) {
        shortenDistanceUsing(section);
        this.downStation = section.upStation();
    }

    private void shortenDistanceUsing(Section sectionInput) {
        this.shortenDistance(sectionInput.distance());
    }

    private void shortenDistance(int distance) {
        this.distance.shorten(distance);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    protected Station upStation() {
        return upStation;
    }

    protected Station downStation() {
        return downStation;
    }

    public List<Station> upDownStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Long upStationId() {
        return upStation.getId();
    }

    public Long downStationId() {
        return downStation.getId();
    }

    public Long lineId() {
        return line.getId();
    }

    public String upStationName() {
        return upStation.getName();
    }

    public String downStationName() {
        return downStation.getName();
    }

    public boolean bothStationsAreAlreadyIn(List<Station> stations) {
        return stations.contains(this.upStation)
                && stations.contains(this.downStation);
    }

    public boolean bothStationsAreNotIn(List<Station> stations) {
        return !stations.contains(this.upStation)
                && !stations.contains(this.downStation);
    }

    public DockingPosition dockingCheck(Section section) {
        if (this.canBeDockedOnFront(section)) {
            return DockingPosition.FRONT;
        }
        if (this.canBeDockedOnRear(section)) {
            return DockingPosition.REAR;
        }
        return DockingPosition.NONE;
    }

    public boolean canBeDockedOnFront(Section section) {
        return checkFront(section) || checkMidFront(section) || checkMidRear(section);
    }

    public boolean canBeDockedOnRear(Section section) {
        return checkRear(section);
    }

    public boolean checkFront(Section section) {
        return this.isInFrontOf(section);
    }

    public boolean checkMidFront(Section section) {
        if (this.isInMidFrontOf(section)) {
            section.connectBehindOf(this);
            return true;
        }
        return false;
    }

    public boolean checkMidRear(Section section) {
        if (this.isInMidRearOf(section)) {
            section.connectInFrontOf(this);
            return true;
        }
        return false;
    }

    public boolean checkRear(Section section) {
        return this.isBehindOf(section);
    }

    public int distance() {
        return distance.get();
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setDistance(int distance) {
        this.distance.set(distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
