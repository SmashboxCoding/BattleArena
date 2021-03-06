package mc.alk.arena.competition.util;

import mc.alk.arena.competition.Competition;
import mc.alk.arena.objects.ArenaParams;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.exceptions.NeverWouldJoinException;
import mc.alk.arena.objects.teams.CompositeTeam;
import mc.alk.arena.objects.teams.Team;

public class TeamJoinFactory {

	public static TeamJoinHandler createTeamJoinHandler(MatchParams params, Competition competition) throws NeverWouldJoinException {
		return createTeamJoinHandler(params,competition,CompositeTeam.class);
	}

	public static TeamJoinHandler createTeamJoinHandler(MatchParams params, Competition competition,
			Class<? extends Team> clazz) throws NeverWouldJoinException {
		if (params.getMaxTeams() != ArenaParams.MAX){ /// we have a finite set of players
			return new AddToLeastFullTeam(params, competition,clazz);	/// lets try and add players to all players first
		} else { /// finite team size
			return new BinPackAdd(params, competition,clazz);
		}
	}
}
