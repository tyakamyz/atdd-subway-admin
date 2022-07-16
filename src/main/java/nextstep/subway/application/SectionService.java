package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SectionService {
    private final SectionRepository sectionRepository;
    private final StationService stationService;

    public SectionService(SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    @Transactional(readOnly = true)
    public Optional<Section> findById(Long id) {
        return sectionRepository.findById(id);
    }

    @Transactional
    public LineResponse saveSection(Line line, SectionRequest sectionRequest) {
        Station upStation = stationService.findById(sectionRequest.getUpStationId());
        Station downStation = stationService.findById(sectionRequest.getDownStationId());

        Sections sections = line.getSections();
        Section section = new Section(upStation, downStation, sectionRequest.getDistance());

        for (Section lineSection : sections.getSections()) {
            validateSections(lineSection, section);
            reregisterStations(lineSection, section);
        }

        line.addSection(section);

        return LineResponse.of(line);
    }

    @Transactional
    public Section reappropriateSection(Section prevSection, Section nextSection) {
        return new Section(prevSection.getUpStation(), nextSection.getDownStation(), prevSection.getDistance() + nextSection.getDistance());
    }

    private void validateSections(Section lineSection, Section section) {
        lineSection.duplicateValidateCheck(section);
        lineSection.mismatchValidateCheck(section);
    }

    private void reregisterStations(Section lineSection, Section section) {
        lineSection.reregisterUpStation(section);
        lineSection.reregisterDownStation(section);
    }

    public Optional<Section> findByUpStationId(Long stationId) {
        return sectionRepository.findByUpStationId(stationId);
    }

    public Optional<Section> findByDownStationId(Long stationId) {
        return sectionRepository.findByDownStationId(stationId);
    }
}
