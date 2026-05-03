package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.dao.ReservationDao;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private ReservationDao reservationDao;

    @Test
    void 중복된_시간을_저장하면_예외가_발생한다() {
        // given
        ReservationTime existTime = new ReservationTime(null, "10:00");
        reservationTimeService.save(existTime);

        // when
        ReservationTime newTime = new ReservationTime(null, "10:00");

        //then
        assertThatThrownBy(() -> reservationTimeService.save(newTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 예약시간입니다.");
    }

    @Test
    void 이미_예약시간이_차있으면_삭제할_수_없다(){
        //given
        ReservationTime existTime = new ReservationTime(null, "10:00");
        ReservationTime savedTime = reservationTimeService.save(existTime);
        Reservation reservation = new Reservation(null, "pobi", "2026-05-02", savedTime);
        reservationDao.save(reservation);
        Long savedId = savedTime.getId();

        //when & then
        assertThatThrownBy(()-> reservationTimeService.deleteById(savedId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제할 수 없습니다");
    }
}
